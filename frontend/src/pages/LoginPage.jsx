import { useState, useEffect, useRef } from "react";
import { useSearchParams } from "react-router-dom";
import loginBg from "../assets/login-bg.jpg";
import { startLogin } from '../services/authService';
import { showErrorToast } from '../services/errorToastStore';

function LoginPage() {
    const [formData, setFormData] = useState({username: '', password: ''});
    const [searchParams, setSearchParams] = useSearchParams();

    const [isLoading, setIsLoading] = useState(false);
    const isSubmittingRef = useRef(false); // Synchronous lock for native form

    useEffect(() => {
        if (!sessionStorage.getItem('pkce_code_verifier')) {
            startLogin();
        }
    }, []);

    // Spring Security redirects here with ?error after a failed native form login.
    useEffect(() => {
        if (searchParams.get('error') !== null) {
            showErrorToast({
                message: 'Invalid email or password',
                errorCode: 401,
            });

            // Strip ?error from the URL so refreshing the page doesn't re-trigger the toast
            searchParams.delete('error');
            setSearchParams(searchParams, {replace: true});
        }
    }, [searchParams, setSearchParams]);

    const handleChange = (e) => {
        setFormData({...formData, [e.target.name]: e.target.value});
    };

    const handleSubmit = (e) => {
        if (isSubmittingRef.current) {
            e.preventDefault(); // Blocks rapid duplicate double-clicks
            return;
        }

        isSubmittingRef.current = true;
        setIsLoading(true);
        // Allows the initial native POST to /login to proceed normally
    };

    return (
        <div
            className="h-screen bg-cover bg-center flex items-center justify-center"
            style={{backgroundImage: `url('${loginBg}')`}}
        >
            {/* Card Container */}
            <div
                className="h-2/3 w-1/3 min-w-[320px] bg-[#fdebe8] text-zinc-950 flex flex-col justify-between items-center p-8 rounded-xl shadow-xl">

                {/* Header: Center 'Login' vertically */}
                <header className="flex-1 flex items-center justify-center">
                    <h1 className="text-4xl font-bold">Login</h1>
                </header>

                {/* Main section carrying form elements */}
                <main className="w-full">
                    <form
                        method="POST"
                        action="/login"
                        onSubmit={handleSubmit}
                        className="flex flex-col gap-4"
                    >

                        {/* Text / Email Input */}
                        <div className="flex flex-col gap-1">
                            <label className="text-sm font-medium">Email</label>
                            <input
                                type="text"
                                name="username"
                                value={formData.username}
                                onChange={handleChange}
                                placeholder="Enter your username"
                                className="w-full px-3 py-2 bg-[#F6F6F6] text-zinc-900 rounded-md border border-zinc-400 focus:outline-none focus:ring-2 focus:ring-teal-700"
                            />
                        </div>

                        {/* Password Input */}
                        <div className="flex flex-col gap-1">
                            <label className="text-sm font-medium">Password</label>
                            <input
                                type="password"
                                name="password"
                                value={formData.password}
                                onChange={handleChange}
                                placeholder="••••••••"
                                className="w-full px-3 py-2 bg-[#F6F6F6] text-zinc-900 rounded-md border border-zinc-400 focus:outline-none focus:ring-2 focus:ring-teal-700"
                            />
                        </div>

                        {/* Submit Button */}
                        <button
                            type="submit"
                            disabled={isLoading}
                            className={`mt-2 w-full py-2.5 bg-teal-800 hover:bg-teal-700 text-white font-semibold rounded-md shadow-md transition-colors ${
                                isLoading ? 'opacity-60 cursor-not-allowed' : 'cursor-pointer'
                            }`}
                        >
                            {isLoading ? 'Signing In...' : 'Sign In'}
                        </button>
                    </form>
                </main>

                {/* Footer */}
                <footer className="flex-1 flex items-center justify-center">
                    <p className="text-sm text-zinc-900">
                        Don't have an account?{" "}
                        <a href="/register" className="text-teal-900 hover:underline font-semibold">
                            Register
                        </a>
                    </p>
                </footer>

            </div>
        </div>
    );
}

export default LoginPage;