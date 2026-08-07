import {useState} from 'react';
import loginBg from "../assets/login-bg.jpg";
import api from '../services/api';

function RegisterPage() {
    const [formData, setFormData] = useState({
        email: '',
        password: ''
    });

    const handleChange = (e) => {
        setFormData({...formData, [e.target.name]: e.target.value});
    };

    const handleSubmit = async (e) => {
        e.preventDefault(); // Prevents standard page reload

        try {
            await api.post("/register", formData);
            console.log("Registration successful!");
            // Add redirect logic here if needed
        } catch (error) {
            console.error("Registration failed:", error.response?.data || error.message);
        }
    };

    return (
        <div
            className="h-screen bg-cover bg-center flex items-center justify-center"
            style={{backgroundImage: `url(${loginBg})`}}
        >
            {/* Card Container */}
            <div
                className="h-2/3 w-1/3 min-w-[320px] bg-[#fdebe8] text-zinc-950 flex flex-col justify-between items-center p-8 rounded-xl shadow-xl">

                {/* Header */}
                <header className="flex-1 flex items-center justify-center">
                    <h1 className="text-4xl font-bold">Register</h1>
                </header>

                {/* Main section carrying form elements */}
                <main className="w-full">
                    <form
                        onSubmit={handleSubmit}
                        className="flex flex-col gap-4"
                    >
                        {/* Email Input */}
                        <div className="flex flex-col gap-1">
                            <label className="text-sm font-medium">Email</label>
                            <input
                                type="email"
                                name="email"
                                value={formData.email}
                                onChange={handleChange}
                                placeholder="Enter your email"
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
                            className="mt-2 w-full py-2.5 bg-teal-800 hover:bg-teal-700 text-white font-semibold rounded-md shadow-md transition-colors cursor-pointer"
                        >
                            Sign Up
                        </button>
                    </form>
                </main>

                {/* Footer */}
                <footer className="flex-1 flex items-center justify-center">
                    <p className="text-sm text-zinc-900">
                        Already have an account?{" "}
                        <a href="/login" className="text-teal-900 hover:underline font-semibold">
                            Login
                        </a>
                    </p>
                </footer>

            </div>
        </div>
    );
}

export default RegisterPage;