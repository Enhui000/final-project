const tabButtons = document.querySelectorAll('.tab-button');
const forms = document.querySelectorAll('.auth-form');
const switchLinks = document.querySelectorAll('[data-switch]');

function activateForm(targetId) {
  forms.forEach((form) => {
    form.classList.toggle('is-active', form.id === targetId);
  });
  tabButtons.forEach((button) => {
    button.classList.toggle('is-active', button.dataset.target === targetId);
  });
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
    const placeholderAlert = form.id === 'login-form'
      ? 'Login logic will connect to the backend API in a future update.'
      : 'Registration logic will connect to the backend API in a future update.';

    alert(placeholderAlert);
  });
});
