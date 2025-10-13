'use client';
import { useState } from 'react';
import LoginForm from '@/components/LoginForm';
import RegisterForm from '@/components/RegisterForm';
import { motion } from 'framer-motion';

export default function AuthPage() {
    const [isLogin, setIsLogin] = useState(true);

    return (
        <main className="flex flex-col items-center justify-center min-h-screen px-4 bg-[var(--background)] text-[var(--foreground)] transition-colors">
            <motion.h1
                initial={{ opacity: 0, y: -10 }}
                animate={{ opacity: 1, y: 0 }}
                className="text-4xl font-bold mb-8 tracking-tight"
            >
                Vault Access
            </motion.h1>

            {isLogin ? (
                <LoginForm onSwitch={() => setIsLogin(false)} />
            ) : (
                <RegisterForm onSwitch={() => setIsLogin(true)} />
            )}

            <footer className="mt-10 text-xs text-gray-500 dark:text-gray-400">
                © {new Date().getFullYear()} SecureVault Inc.
            </footer>
        </main>
    );
}
