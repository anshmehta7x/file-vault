'use client';
import {useAuth} from "@/utils/AuthContext";
import { redirect } from 'next/navigation';

export default function Home() {
    const {isLoggedIn, userId, userName  } = useAuth()

    if(!isLoggedIn){
        redirect('/auth')
    }



    return (
      <>
      </>
  );
}
