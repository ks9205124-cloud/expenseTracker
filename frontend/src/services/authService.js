// authService.js

export function generateCodeVerifier() {
    const array = new Uint8Array(32);
    crypto.getRandomValues(array);
    return btoa(String.fromCharCode(...array))
        .replace(/\+/g, '-')
        .replace(/\//g, '_')
        .replace(/=+$/, '');
}

export async function generateCodeChallenge(verifier) {
    const encoder = new TextEncoder();
    const data = encoder.encode(verifier);
    const digest = await crypto.subtle.digest('SHA-256', data);
    return btoa(String.fromCharCode(...new Uint8Array(digest)))
        .replace(/\+/g, '-')
        .replace(/\//g, '_')
        .replace(/=+$/, '');
}

export async function startLogin() {
    const verifier = generateCodeVerifier();
    const challenge = await generateCodeChallenge(verifier);

    sessionStorage.setItem('pkce_code_verifier', verifier);

    const params = new URLSearchParams({
        response_type: 'code',
        client_id: 'client',
        scope: 'openid',
        redirect_uri: 'http://localhost:5173/callback',
        code_challenge: challenge,
        code_challenge_method: 'S256'
    });

    window.location.href = `http://localhost:8080/oauth2/authorize?${params.toString()}`;
}