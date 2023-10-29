import {Point, PointStore} from "@/app/api/PointsApi";
import {create} from "zustand";

export const usePoints = create<PointStore>((set) => ({
    points: [],
    add: (by) =>
        set((state) =>
            ({ points: [...state.points, by] })),
}))