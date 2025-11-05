const tabButtons = document.querySelectorAll('.tab-button');
const forms = document.querySelectorAll('.auth-form');
const switchLinks = document.querySelectorAll('[data-switch]');

const API_ENDPOINTS = {
  'login-form': '/api/auth/login',
  'register-form': '/api/auth/register',
};

const STORAGE_KEYS = {
  accessToken: 'knot.accessToken',
  refreshToken: 'knot.refreshToken',
  username: 'knot.username',
  userId: 'knot.userId',
};

const DEFAULT_INFO_MESSAGES = {
  'login-form': 'Sign in will call the Spring Boot API (backed by MySQL and Redis). Ensure the server is running before continuing.',
  'register-form': 'Creating a new account persists your profile in MySQL and seeds refresh tokens in Redis once you sign in.',
};

function activateForm(targetId) {
  forms.forEach((form) => {
    const wasActive = form.classList.contains('is-active');
    const isActive = form.id === targetId;
    form.classList.toggle('is-active', isActive);
    if (isActive && !wasActive) {
      resetAlert(form);
      showDefaultInfo(form);
      const focusTarget = form.querySelector('input[type="text"], input[type="password"]');
      focusTarget?.focus();
    }
  });
  tabButtons.forEach((button) => {
    button.classList.toggle('is-active', button.dataset.target === targetId);
  });
}

function showDefaultInfo(form) {
  const message = DEFAULT_INFO_MESSAGES[form.id];
  if (message) {
    showAlert(form, 'info', message);
  }
}

function getAlert(form) {
  return form.querySelector('.form-alert');
}

function resetAlert(form) {
  const alert = getAlert(form);
  if (!alert) return;
  alert.hidden = true;
  alert.textContent = '';
  alert.classList.remove('form-alert--error', 'form-alert--success', 'form-alert--info');
}

function showAlert(form, type, message) {
  const alert = getAlert(form);
  if (!alert) return;
  alert.hidden = false;
  alert.textContent = message;
  alert.classList.remove('form-alert--error', 'form-alert--success', 'form-alert--info');
  alert.classList.add(`form-alert--${type}`);
}

function setFormLoading(form, isLoading) {
  const submitButton = form.querySelector('button[type="submit"]');
  if (!submitButton) return;
  submitButton.disabled = isLoading;
  submitButton.classList.toggle('is-loading', isLoading);
}

function buildPayload(form) {
  const formData = new FormData(form);
  const username = (formData.get('username') || '').trim();
  const password = formData.get('password') || '';

  if (!username || !password) {
    showAlert(form, 'error', 'Username and password are both required.');
    return null;
  }

  if (password.length < 8) {
    showAlert(form, 'error', 'Password must contain at least 8 characters.');
    return null;
  }

  if (form.id === 'register-form') {
    const confirmPassword = formData.get('confirmPassword') || '';
    if (password !== confirmPassword) {
      showAlert(form, 'error', 'Passwords do not match.');
      return null;
    }
  }

  const remember = formData.get('remember') === 'on';
  return { username, password, remember };
}

async function parseJsonResponse(response) {
  try {
    return await response.json();
  } catch (error) {
    return null;
  }
}

function persistLogin(tokenResponse, remember) {
  const targetStorage = remember ? window.localStorage : window.sessionStorage;
  const alternateStorage = remember ? window.sessionStorage : window.localStorage;

  const entries = [
    [STORAGE_KEYS.accessToken, tokenResponse.accessToken],
    [STORAGE_KEYS.refreshToken, tokenResponse.refreshToken],
    [STORAGE_KEYS.username, tokenResponse.username],
    [STORAGE_KEYS.userId, tokenResponse.userId?.toString() ?? ''],
  ];

  entries.forEach(([key]) => {
    try {
      alternateStorage.removeItem(key);
    } catch (error) {
      // Ignore removal errors (e.g., disabled storage)
    }
  });

  entries.forEach(([key, value]) => {
    if (value == null) return;
    try {
      targetStorage.setItem(key, value);
    } catch (error) {
      // Surface storage failures to the console for troubleshooting
      console.warn('Unable to persist authentication data', { key, error });
    }
  });
}

async function submitForm(form) {
  const payload = buildPayload(form);
  if (!payload) return;

  const endpoint = API_ENDPOINTS[form.id];
  if (!endpoint) return;

  resetAlert(form);
  setFormLoading(form, true);

  try {
    const response = await fetch(endpoint, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify({
        username: payload.username,
        password: payload.password,
      }),
    });

    const body = await parseJsonResponse(response);
    const apiSuccess = body?.success === true;

    if (!response.ok || !apiSuccess) {
      const errorMessage = body?.error || body?.message || 'Request failed, please verify your credentials and try again.';
      showAlert(form, 'error', errorMessage);
      return;
    }

    if (form.id === 'register-form') {
      showAlert(
        form,
        'success',
        `Account for "${payload.username}" created successfully. You can sign in with your new credentials.`,
      );
      form.reset();

      const loginForm = document.getElementById('login-form');
      const loginUsername = document.getElementById('login-username');
      if (loginUsername) {
        loginUsername.value = payload.username;
      }

      setTimeout(() => {
        activateForm('login-form');
        loginForm?.querySelector('input[name="password"]').focus();
      }, 600);
      return;
    }

    const tokenResponse = body?.data;
    if (!tokenResponse?.accessToken || !tokenResponse?.refreshToken) {
      showAlert(form, 'error', 'Login succeeded but no tokens were returned by the server.');
      return;
    }

    persistLogin(tokenResponse, payload.remember);
    showAlert(
      form,
      'success',
      'Login successful! Your tokens have been stored locally. You can now continue to the dashboard.',
    );
  } catch (error) {
    console.error('Authentication request failed', error);
    showAlert(form, 'error', 'Unable to reach the server. Please confirm the backend is running and try again.');
  } finally {
    setFormLoading(form, false);
  }
}

tabButtons.forEach((button) => {
  button.addEventListener('click', () => {
    activateForm(button.dataset.target);
  });
});

switchLinks.forEach((link) => {
  link.addEventListener('click', (event) => {
    event.preventDefault();
    activateForm(link.dataset.switch);
  });
});

forms.forEach((form) => {
  form.addEventListener('submit', (event) => {
    event.preventDefault();
    submitForm(form);
  });
});

forms.forEach((form) => {
  if (form.classList.contains('is-active')) {
    showDefaultInfo(form);
  }
});

// Focus username field on initial load for convenience
document.getElementById('login-username')?.focus();
