import { useState, useRef } from 'react';

export default function AsyncButton({ onClick, children, style, ...props }) {
    const [isLoading, setIsLoading] = useState(false);
    const isProcessingRef = useRef(false); // Synchronous lock

    const handleClick = async (e) => {
        // Blocks duplicate clicks instantly within the same microsecond
        if (isProcessingRef.current) return;

        isProcessingRef.current = true;
        setIsLoading(true);

        try {
            await onClick(e);
        } finally {
            isProcessingRef.current = false;
            setIsLoading(false);
        }
    };

    return (
        <button
            onClick={handleClick}
            disabled={isLoading}
            style={{
                ...style,
                opacity: isLoading ? 0.6 : 1,
                cursor: isLoading ? 'not-allowed' : 'pointer'
            }}
            {...props}
        >
            {isLoading ? 'Processing...' : children}
        </button>
    );
}