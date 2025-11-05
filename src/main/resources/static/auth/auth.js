const tabButtons = document.querySelectorAll('.tab-button');
const forms = document.querySelectorAll('.auth-form');
const switchLinks = document.querySelectorAll('[data-switch]');

const DEFAULT_INFO_MESSAGES = {
  'login-form': 'Sign in with your username and password. Backend wiring will be added later.',
  'register-form': 'Create a username and password to mock the registration experience. Integration is coming soon.',
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

function validateForm(form) {
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

  return { username, password };
}

function handleSubmit(form) {
  const result = validateForm(form);
  if (!result) {
    return;
  }

  if (form.id === 'register-form') {
    showAlert(
      form,
      'success',
      `Placeholder success! An account for "${result.username}" would be created once the backend is connected.`,
    );
    return;
  }

  showAlert(
    form,
    'success',
    `Placeholder login succeeded for "${result.username}". The actual authentication call will be hooked up later.`,
  );
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
    handleSubmit(form);
  });
});

forms.forEach((form) => {
  if (form.classList.contains('is-active')) {
    showDefaultInfo(form);
  }
});

document.getElementById('login-username')?.focus();
