import {useState, useEffect} from "react";
import loginBg from "../assets/login-bg.jpg";
import {startLogin} from '../services/authService';

function LoginPage() {
    const [formData, setFormData] = useState({username: '', password: ''});

    useEffect(() => {
        if (!sessionStorage.getItem('pkce_code_verifier')) {
            startLogin();
        }
    }, []);

    const handleChange = (e) => {
        setFormData({...formData, [e.target.name]: e.target.value});
    };
    const handleSubmit = (e) => {
        e.preventDefault();
        console.log("i tried to submit");
    };
    return (/* Outer Screen Wrapper */
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
                        action="http://localhost:8080/login"
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
                        {/* Remove onSubmit from here */}
                        <button
                            type="submit"
                            className="mt-2 w-full py-2.5 bg-teal-800 hover:bg-teal-700 text-white font-semibold rounded-md shadow-md transition-colors cursor-pointer"
                        >
                            Sign In
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
        </div>);
}

export default LoginPage;