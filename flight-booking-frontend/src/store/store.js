import {configureStore} from "@reduxjs/toolkit";
import searchReducer from './searchSlice.js'

export const store = configureStore({
    reducer: {
        search: searchReducer,
    },
})