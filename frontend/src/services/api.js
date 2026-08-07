import axios from 'axios';

const api = axios.create({
    baseURL: '',
    withCredentials: true, // Crucial for sending cookies/sessions
});

export default api;