import axios, {Axios, AxiosResponse} from 'axios'
import { create } from 'zustand'

const useUserDataStore = create<UserData>()((set) => ({
    login: undefined,
    password: undefined
}))

const SERVER_URL = '/proxy'
export interface UserData {
    login: string | undefined
    password: string | undefined
}

export interface TokenInfo {
    user: string | undefined,
    expires: number | undefined
}

export const useTokenStore = create<TokenInfo>((set) => ({
    user: undefined,
    expires: undefined
}))

export function loginUser(user: UserData, onResult: (err: String | null) => void) {
    const res = axios(SERVER_URL + '/login', {
        method: 'POST',
        headers: {
            'Accept': 'application/json, text/plain, */*',
            'Content-Type': 'application/json',
            withCredentials: true
        },
        data: JSON.stringify(user)
    }).then(res => {
        console.log(res)
        if(res.data.success)
            useTokenStore.setState({user: res.data.result.user, expires: res.data.result.expires})
        console.log(useTokenStore.getState())
        onResult(res.data.success ? null : res.data.error)
    }).catch(err => {
        onResult(err.message)
    })
}

export function registerUser(user: UserData, onResult: (err: String | null) => void) {
    console.log(user);
    const res = axios(SERVER_URL + '/register', {
        method: 'POST',
        headers: {
            'Accept': 'application/json, text/plain, */*',
            'Content-Type': 'application/json',
            withCredentials: true
        },
        data: JSON.stringify(user)
    }).then(res => {
        onResult(res.data.success ? null : res.data.error)
    }).catch(err => {
        onResult(err.message)
    })
}

export function logoutUser() {
    useTokenStore.setState({user: undefined, expires: undefined})
    const res = axios(SERVER_URL + '/logout').then(
        res => console.log(res)
    )
}