// ErrorToastContainer.jsx
// Mount this once near the root of your app (e.g. in App.jsx, alongside your router).
// It listens for showErrorToast() calls and renders auto-dismissing popups.

import { useEffect, useState } from 'react';
import { subscribe } from '../services/errorToastStore';

const DISPLAY_DURATION_MS = 5000;

export default function ErrorToastContainer() {
    const [toasts, setToasts] = useState([]);

    useEffect(() => {
        const unsubscribe = subscribe((toast) => {
            setToasts((prev) => [...prev, toast]);

            setTimeout(() => {
                setToasts((prev) => prev.filter((t) => t.id !== toast.id));
            }, DISPLAY_DURATION_MS);
        });

        return unsubscribe;
    }, []);

    const dismiss = (id) => {
        setToasts((prev) => prev.filter((t) => t.id !== id));
    };

    if (toasts.length === 0) return null;

    return (
        <div className="fixed top-4 right-4 z-50 flex flex-col gap-2 w-80">
            {toasts.map((toast) => (
                <div
                    key={toast.id}
                    className="bg-red-50 border border-red-300 text-red-800 rounded-lg shadow-lg p-4 flex items-start justify-between gap-3 animate-[fadeIn_0.2s_ease-out]"
                >
                    <div className="flex-1">
                        <p className="text-sm font-medium">{toast.message}</p>
                        {toast.errorCode && (
                            <p className="text-xs text-red-500 mt-1">Error code: {toast.errorCode}</p>
                        )}
                    </div>
                    <button
                        onClick={() => dismiss(toast.id)}
                        className="text-red-400 hover:text-red-700 text-lg leading-none"
                        aria-label="Dismiss"
                    >
                        &times;
                    </button>
                </div>
            ))}
        </div>
    );
}