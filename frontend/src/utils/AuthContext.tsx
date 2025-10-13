'use client';
import React, { createContext, useContext, useState, ReactNode } from "react";

interface AuthState{
    isLoggedIn: boolean;
    userId?: string;
    userName? :string;
    token?: string;

    login: (token: string, userId: string, userName: string) => void;
    logout: () => void;
}

const AuthContext = createContext<AuthState | undefined>(undefined);

export const AuthProvider  = ({
    children
} : {
    children: ReactNode
}) =>{
    const [token, setToken] = useState<string | undefined>(undefined);
    const [userId, setUserId] = useState<string | undefined>(undefined);
    const [userName, setUserName] = useState<string | undefined>(undefined);

    const isLoggedIn = !!token;

    const login = (newToken: string, newUserId: string, newUserName: string) =>{
        setToken(newToken);
        setUserId(newUserId);
        setUserName(newUserName);
        localStorage.setItem("authToken", newToken);
        localStorage.setItem("userId", newUserId);
        localStorage.setItem("userName", newUserName);
    }

    const logout = () =>{
        setToken(undefined);
        setUserId(undefined);
        setUserName(undefined);
        localStorage.removeItem("authToken");
        localStorage.removeItem("userId");
        localStorage.removeItem("userName");

    }


    React.useEffect(() => {
        const storedToken = localStorage.getItem("authToken");
        const storedUserId = localStorage.getItem("userId");
        const storedUserName = localStorage.getItem("userName");
        if (storedToken && storedUserId && storedUserName) {
            setToken(storedToken);
            setUserId(storedUserId);
            setUserName(storedUserName);


        }
    },[]);


    return <AuthContext.Provider value={{token,userId,userName,isLoggedIn,login,logout}}>
        {children}
    </AuthContext.Provider>
}

export const useAuth = () => {
    const context = useContext(AuthContext);
    if (!context) {
        throw new Error("useAuth must be used inside an AuthProvider");
    }
    return context;
};
