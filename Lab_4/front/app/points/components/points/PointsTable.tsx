import {Point, PointStore} from "@/app/api/PointsApi";
import {usePoints} from "@/app/api/usePointsStore";
import "../../css/PointsTable.css"
import {useRef, useState} from "react";


function PointsTable({points}: { points: Point[] }) {
    const [timeFormat, setTimeFormat] = useState(0)
    return (
        <div className="table-container">
            <table className="dots-table">
                <thead>
                <tr>
                    <th>x</th>
                    <th>y</th>
                    <th>r</th>
                    <th style={{padding: 0}}>
                        <label className="custom-select">
                            <select className="table-time-format" onChange={e => setTimeFormat(+e.currentTarget.value)}>
                                <option value="0">timestamp, ms</option>
                                <option value="1">Дата и время</option>
                                <option value="2">Дата и время (кратко)</option>
                                <option value="3">Точное время</option>
                            </select>
                            <svg>
                                <use href="#select-arrow-down"></use>
                            </svg>
                        </label></th>
                    <th>Время выполнения, ms</th>
                </tr>
                </thead>
                <tbody>
                {points.map((e, i) => {
                    return (
                        <tr key={i} style={{color: e.success ? "#00c905" : "red"}}>
                            <td>{e.x.toFixed(3)}</td>
                            <td>{e.y.toFixed(3)}</td>
                            <td>{e.r}</td>
                            <td>{
                                timeFormat === 0 ?
                                    e.timestamp :
                                    timeFormat === 1 ?
                                        new Date(e.timestamp).toString() :
                                        timeFormat === 2 ?
                                            (() => {
                                                let date = new Date(e.timestamp);
                                                return (date.getDate() < 10 ? "0" : "") + date.getDate() + "."
                                                    + ((date.getMonth() + 1) < 10 ? "0" : "") + (date.getMonth() + 1) + "."
                                                    + date.getFullYear() + " "
                                                    + date.toLocaleTimeString()
                                            })()
                                            :
                                            (() => {
                                                const date = new Date(points[i].timestamp);
                                                return date.toLocaleTimeString() + "." + date.getMilliseconds();
                                            })()
                            } </td>
                            <td>{e.executionTime}</td>
                        </tr>
                    )
                })}
                <tr className="points-table-filler">
                    <td style={{height: "auto"}} colSpan={5}></td>
                </tr>
                </tbody>
            </table>
        </div>
    )
}

export default PointsTable