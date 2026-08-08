import axios from 'axios';
import { showErrorToast } from './errorToastStore';

const api = axios.create({
    baseURL: '',
    withCredentials: true, // Crucial for sending cookies/sessions
});

api.interceptors.response.use(
    (response) => response,
    (error) => {
        // Matches GlobalExceptionHandler's shape: { timestamp, status, error, message }
        const data = error.response?.data;

        showErrorToast({
            message: data?.message || error.message || 'Request failed',
            errorCode: data?.error || data?.status || error.response?.status,
        });

        return Promise.reject(error);
    }
);

export default api;