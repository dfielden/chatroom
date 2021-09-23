import {AJAX} from "./ajax.js";
const loginBtn = document.querySelector('.btn');
const LOGIN_SUCCESS_VALUE = 'LOGIN_SUCCESS';

loginBtn.addEventListener('click', (e) => {
    e.preventDefault();
    login();
})

const login = async () => {
    const username = document.querySelector('.form-input').value.trim();
    console.log(username)
    if (username) {
        document.querySelector('.form-input').value = "";
        const data = await AJAX("/login", username);
        if (data === LOGIN_SUCCESS_VALUE) {
            window.location.href = "/";
        }
    }
}
