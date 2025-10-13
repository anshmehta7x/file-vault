import { config } from "@/utils/Config";

export async function register(username: string, password: string) {
    try {
        const result = await fetch(`http://${config.BACKEND_URL}/user/register`, {
            method: "POST",
            headers: {
                "Content-Type": "application/json",
            },
            body: JSON.stringify({ username, password }),
        });

        const res = await result.json();

        if (!result.ok) {
            return { error: res.message || "Registration failed" };
        }

        return res;
    } catch (err: unknown) {
        return {
            error: err instanceof Error ? err.message : String(err),
        };
    }
}

export async function loginRoute(username: string, password: string) {
    try {
        const result = await fetch(`http://${config.BACKEND_URL}/user/login`, {
            method: "POST",
            headers: {
                "Content-Type": "application/json",
            },
            body: JSON.stringify({ username, password }),
        });

        const res = await result.json();

        if (!result.ok) {
            return { error: res.message || "Registration failed" };
        }

        return res;
    } catch (err: unknown) {
        return {
            error: err instanceof Error ? err.message : String(err),
        };
    }
}
