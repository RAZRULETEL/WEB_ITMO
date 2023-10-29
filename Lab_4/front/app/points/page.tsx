'use client'
import "./css/NiceSelect.css"
import styles from "./css/page.module.css"
import {create} from 'zustand'
import {usePoints} from "@/app/api/usePointsStore";
import {clearPoints, getAll, RawPoint, sendPoint} from "@/app/api/PointsApi";
import PointsTable from "@/app/points/components/points/PointsTable";
import React, {useEffect, useRef, useState} from "react";
import CompactRadioCheckbox from "@/app/points/components/CompactRadioCheckbox";
import "./css/PointsPage.css"
import SpiralInputText from "@/app/points/components/SpiralInputText";
import PointsCanvas from "@/app/points/components/points/PointsCanvas";
import Label, {LabelStore} from "@/app/points/components/StatusLabel";
import {logoutUser, UserData} from "@/app/api/AuthApi";
import bg from "@/public/bg.jpg";
import settings from "@/public/setting.png";

const useRawPointStore = create<RawPoint>()((set) => ({
    x: null,
    y: null,
    r: null,
    setR: (r) => set((state) => ({r: r})), // For cause re-render
}))

const useLabelStore = create<LabelStore>((set) => ({
    text: "",
    x: 0,
    y: 0,
    visible: false,
    updateVisible: (visible) => set((state) => ({visible: visible})),
    setCoordinates: (x, y) => set((state) => ({x: x, y: y})),
    updateText: (text) => set((state) => ({text: text}))
}))


export default function Home() {
    const usePoint = useRawPointStore()
    const useLabel = useLabelStore()
    const [showRadiusOnly, setShowRadiusOnly] = useState(false)

    useEffect(() => {
        getAll()
    })

    return (
        <>
            <Label label={useLabel}/>
            <main className={styles.content} style={{backgroundImage: `url(${bg.src})`}}>
                <div className={styles["main-content"]}>
                    <PointsCanvas
                        points={usePoints().points}
                        radius={usePoint.r}
                        onClick={e => {
                            if (e.r == null) return;
                            useLabel.updateVisible(true);
                            useLabel.updateText(`x: ${e.x?.toFixed(2)} y: ${e.y?.toFixed(2)} R: ${e.r}`);
                            sendPoint(e, e => {
                                if (e == null) {
                                    useLabel.updateVisible(false);
                                    return
                                }
                                useLabel.updateText((e.success ? "✅" : "❌") + useLabelStore.getState().text + (e.success ? "✅" : "❌"))
                            })
                        }}
                        label={useLabel}
                        showOnlyRadiusLayer={showRadiusOnly}
                    />
                    <PointsTable points={usePoints().points}/>
                </div>
                <div className={styles.form}>
                    <div className={styles["radio-checkbox-container"]}>
                        <CompactRadioCheckbox<number>
                            values={[-2, -1.5, -1, -0.5, 0, 0.5, 1, 1.5, 2]}
                            setValue={e => {
                                usePoint.x = e
                            }}/>
                    </div>
                    <SpiralInputText placeholder="y"
                                     onChange={e => {
                        const value = e.value.replace(",", ".")
                        // @ts-ignore
                        if (isNaN(value)) {
                            e.setCustomValidity("Некорректное значение, пожалуйста, введите число")
                            e.reportValidity()
                            usePoint.y = null
                        } else {
                            e.setCustomValidity("")
                            usePoint.y = +value
                        }
                    }}/>
                    <div className={styles["radio-checkbox-container"]}>
                        <CompactRadioCheckbox<number>
                            values={[-2, -1.5, -1, -0.5, 0, 0.5, 1, 1.5, 2]}
                            setValue={e => {
                                usePoint.setR(e)
                            }}/>
                    </div>
                </div>
                <div className={styles.buttonContainer}>
                    <label title="Show points with currently selected radius only" style={{
                        cursor: 'pointer',
                        userSelect: 'none',
                        display: 'flex',
                        alignItems: 'center',
                    }}
                    >
                        <input type="checkbox" onChange={e => setShowRadiusOnly(e.currentTarget.checked)}/>
                        <span style={{paddingLeft: '4px'}}>current radius only</span>
                    </label>
                    <button
                        title="Return to auth page"
                        onClick={() => {
                            logoutUser()
                            window.location.replace("/")
                        }}
                    >
                        Logout
                    </button>
                    <button onClick={() => {
                        clearPoints();
                        useLabel.updateVisible(false)
                    }}>
                        Clear
                    </button>
                    <button onClick={(e) => {
                        if (usePoint.x === null || usePoint.y === null || usePoint.r === null) {
                            e.currentTarget.setCustomValidity("Заполните все поля корректными данными")
                            e.currentTarget.reportValidity()
                            return
                        }
                        e.currentTarget.setCustomValidity("")
                        sendPoint(usePoint)
                    }}>
                        Send
                    </button>
                </div>
            </main>
            <svg className="sprites">
                <symbol id="select-arrow-down" viewBox="0 0 10 6">
                    <polyline points="1 1 5 5 9 1"></polyline>
                </symbol>
            </svg>
        </>
    )
}
