'use client';
import { useState } from 'react';
import { motion } from 'framer-motion';
import {useRouter} from "next/navigation";
import {useAuth} from "@/utils/AuthContext";
import {loginRoute} from "@/utils/api/Auth";

export default function LoginForm({ onSwitch }: { onSwitch: () => void }) {
    const [username, setUsername] = useState('');
    const [password, setPassword] = useState('');
    const [error, setError] = useState('');
    const router = useRouter();
    const {login, logout} = useAuth();

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();
        setError('');

        try {
            const res = await loginRoute(username, password);
            if (res.error) {
                setError(res.error);
            } else {
                console.log('Registered successfully:', res);
                login(res.token,res.userId,username);
                router.push("/vault");
            }
        } catch (err) {
            setError(err instanceof Error ? err.message : String(err));
            logout();
        }
    };

    return (
        <motion.div
            initial={{ opacity: 0, y: 30 }}
            animate={{ opacity: 1, y: 0 }}
            className="w-full max-w-sm p-8 rounded-2xl backdrop-blur-xl border border-[var(--card-border)] shadow-lg bg-[var(--card-bg)]"
        >
            <h2 className="text-3xl font-semibold text-center mb-6 tracking-tight">
                Sign In
            </h2>

            {error && (
                <div className="bg-red-500/10 border border-red-500/30 text-red-500 text-sm rounded-md px-3 py-2 mb-4">
                    {error}
                </div>
            )}

            <form onSubmit={handleSubmit} className="space-y-4">
                <div>
                    <input
                        type="text"
                        placeholder="Username"
                        value={username}
                        onChange={(e) => setUsername(e.target.value)}
                        className="w-full bg-transparent border border-gray-300/40 dark:border-gray-700 rounded-lg px-4 py-2 focus:outline-none focus:ring-2 focus:ring-[var(--accent)] transition"
                    />
                </div>
                <div>
                    <input
                        type="password"
                        placeholder="Password"
                        value={password}
                        onChange={(e) => setPassword(e.target.value)}
                        className="w-full bg-transparent border border-gray-300/40 dark:border-gray-700 rounded-lg px-4 py-2 focus:outline-none focus:ring-2 focus:ring-[var(--accent)] transition"
                    />
                </div>
                <button
                    type="submit"
                    className="w-full bg-[var(--accent)] text-white py-2.5 rounded-lg font-medium hover:bg-blue-700 transition"
                >
                    Continue
                </button>
            </form>

            <p className="text-center text-sm mt-6">
                Don’t have an account?{' '}
                <button
                    onClick={onSwitch}
                    className="text-[var(--accent)] font-medium hover:underline"
                >
                    Register
                </button>
            </p>
        </motion.div>
    );
}
