// errorToastStore.js
// Lightweight event emitter so non-React code (like an axios interceptor)
// can trigger the error popup without needing React context.

let listeners = [];

export function subscribe(listener) {
    listeners.push(listener);
    return () => {
        listeners = listeners.filter((l) => l !== listener);
    };
}

// Call this from anywhere (interceptor, catch blocks, etc.)
// errorCode is optional — pass whatever field your backend JSON uses.
export function showErrorToast({ message, errorCode }) {
    const toast = {
        id: crypto.randomUUID(),
        message: message || 'Something went wrong',
        errorCode: errorCode || null,
    };
    listeners.forEach((listener) => listener(toast));
}