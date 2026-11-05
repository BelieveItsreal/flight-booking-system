import {useDispatch, useSelector} from "react-redux";
import type {AppDispatch, RootState} from "./store.ts";

// Typed versions of the react-redux hooks; use these instead of the plain ones
export const useAppDispatch = useDispatch.withTypes<AppDispatch>()
export const useAppSelector = useSelector.withTypes<RootState>()
