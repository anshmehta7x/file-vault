interface Config {
    BACKEND_URL: string | undefined;
}

const config: Config = {
    BACKEND_URL: process.env.NEXT_PUBLIC_BACKEND_URL,
};

export { config };
