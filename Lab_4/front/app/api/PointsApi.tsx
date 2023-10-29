import axios, {Axios, AxiosResponse} from 'axios'
import {usePoints} from "@/app/api/usePointsStore";
import {func} from "prop-types";
import {useTokenStore} from "@/app/api/AuthApi";

const SERVER_URL = '/proxy/api'
let isPointsLoaded = false;
export interface Point{
    x: number,
    y: number,
    r: number,
    success: boolean,
    timestamp: number,
    executionTime: number
}

export interface PointStore{
    points: Point[],
    add: (by: Point) => void
}

export interface RawPoint {
    x: number | null
    y: number | null
    r: number | null
    setR: (by: number | null) => void
}

export function getAll() {
    if(!checkToken()) window.location.replace("/");
    if (!isPointsLoaded)
    axios(SERVER_URL + '/get')
        .then(res => {
            const points: Array<Point> = res.data.result;
            usePoints.setState({points: points});
            isPointsLoaded = true;
        }).catch(err => {
            window.location.replace("/");
        })
}

export function sendPoint(point: RawPoint, onResult?: (result: Point | null) => void) {
    if(!checkToken()) window.location.replace("/");
    axios(SERVER_URL + '/check', {
        method: 'POST',
        headers: {
            'Accept': 'application/json, text/plain, */*',
            'Content-Type': 'application/json',
            withCredentials: true
        },
        data: JSON.stringify(point)
    }).then(res => {
        if (onResult) onResult(res.data.result);
        if(res.data.success)
            usePoints.getState().add(res.data.result)
    }).catch(err => {
        if (onResult) onResult(null)
    })
}

export function clearPoints(){
    if(!checkToken()) window.location.replace("/");
    axios(SERVER_URL + '/clear')
        .then(res => {
            if(res.data.success)
                usePoints.setState({points: []});
        }).catch(err => {
            usePoints.setState({points: []});
            window.location.replace("/");
        })
}

function checkToken(): boolean{
    const token = useTokenStore.getState();
    return token.user != undefined && token.expires != undefined && token.expires > new Date().getMilliseconds();
}