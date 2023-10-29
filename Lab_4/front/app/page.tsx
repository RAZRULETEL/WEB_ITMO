'use client'
import styles from './page.module.css'
import {create} from 'zustand'
import {loginUser, UserData, registerUser} from "@/app/api/AuthApi";
import {useRouter} from 'next/navigation'
import {useState} from "react";
import RainbowText from "@/app/RainbowText";

const useUserDataStore = create<UserData>()((set) => ({
    login: undefined,
    password: undefined
}))


export default function Home() {
    const useUser = useUserDataStore()
    const router = useRouter()
    const [error, setError] = useState<String>("")
    const [successMessage, setSuccessMessage] = useState<String>("")
    return (
        <>
            <main className={styles.main}>
                <div style={{textAlign: "center"}}>
                    <RainbowText TextTagContainer="h1" text="Лабораторная №4"/>
                    <RainbowText TextTagContainer="h2" text="P3232"/>
                    <RainbowText TextTagContainer="h2" text="Мартыненко Вадим Андреевич"/>
                    <RainbowText TextTagContainer="h2" text="Вариант 678678"/>
                </div>
                <div className={styles.form}>
                    <p style={{display: error === "" ? "none" : "block"}} className={styles.error}>{error}</p>
                    <p style={{display: successMessage === "" || error !== "" ? "none" : "block"}} className={styles.success}>{successMessage}</p>
                    <div className={styles.inputs}>
                        <input placeholder="Login" type="text" onInput={(e) => useUser.login = e.currentTarget.value}/>
                        <input placeholder="Password" type="password"
                               onInput={(e) => useUser.password = e.currentTarget.value}/>
                    </div>

                    <div className={styles.actions}>
                        <button onClick={() => {
                            if (!useUser.login || !useUser.password) {
                                setError("Login and password are required")
                                return
                            }
                            loginUser(useUser, (err) => {
                                if (!err)
                                    router.push("/points")
                                else
                                    setError(err)
                            })
                        }}>Login
                        </button>
                        <button onClick={() => {
                            if (!useUser.login || !useUser.password) {
                                setError("Login and password are required")
                                return
                            }
                            registerUser(useUser, (err) => {
                                if (!err) {
                                    setError("")
                                    setSuccessMessage(`You have successfully registered as ${useUser.login}`)
                                }else
                                    setError(err)
                            })
                        }}>Register
                        </button>
                    </div>
                </div>
            </main>
        </>
    )
}
