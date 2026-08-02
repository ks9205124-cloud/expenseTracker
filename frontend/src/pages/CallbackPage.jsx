import { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';

function CallbackPage() {
    const navigate = useNavigate();
    const [error, setError] = useState(null);

    useEffect(() => {
        const exchangeCode = async () => {
            const params = new URLSearchParams(window.location.search);
            const code = params.get('code');
            const verifier = sessionStorage.getItem('pkce_code_verifier');

            if (!code || !verifier) {
                setError('Missing code or verifier');
                return;
            }

            try {
                const response = await fetch('http://localhost:8080/oauth2/token', {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/x-www-form-urlencoded',
                        'Authorization': 'Basic ' + btoa('client:my-local-secret')
                    },
                    body: new URLSearchParams({
                        grant_type: 'authorization_code',
                        code: code,
                        redirect_uri: 'http://localhost:5173/callback',
                        code_verifier: verifier,
                        client_id: 'client'
                    })
                });

                if (!response.ok) {
                    throw new Error('Token exchange failed');
                }

                const data = await response.json();
                sessionStorage.setItem('access_token', data.access_token);
                sessionStorage.removeItem('pkce_code_verifier');

                navigate('/dashboard');
            } catch (err) {
                setError(err.message);
            }
        };

        exchangeCode();
    }, [navigate]);

    if (error) {
        return <div>Error logging in: {error}</div>;
    }

    return <div>Logging you in...</div>;
}

export default CallbackPage;