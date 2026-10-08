import {findAirport, type Airport} from "../data/airports.ts";
import {toISODate} from "../utils/date.ts";
import {createSlice, type PayloadAction} from "@reduxjs/toolkit";

export type CabinClass = 'Economy' | 'Business'

export interface Travellers {
    adults: number
    children: number
    infants: number
    cabinClass: CabinClass
}

export interface SearchState {
    from: Airport | null
    to: Airport | null
    departure: string | null // 'yyyy-MM-dd'
    returnDate: string | null // 'yyyy-MM-dd'
    travellers: Travellers
}

const initialState: SearchState = {
    from: findAirport('DEL'),
    to: findAirport('BOM'),
    departure: toISODate(new Date()),
    returnDate: null,
    travellers: {
        adults: 1,
        children: 0,
        infants: 0,
        cabinClass: 'Economy',
    },
}
const searchSlice = createSlice({
    name: 'search',
    initialState,
    reducers: {
        setFrom(state, action: PayloadAction<Airport | null>){
            state.from = action.payload
        },
        setTo(state, action: PayloadAction<Airport | null>){
            state.to = action.payload
        },
        swapCities(state){
            const oldFrom = state.from
            state.from = state.to
            state.to = oldFrom
        },
        setDeparture(state, action: PayloadAction<string>){
            state.departure = action.payload
            if (state.returnDate && state.returnDate < action.payload){
                state.returnDate = null
            }
        },
        setReturnDate(state, action: PayloadAction<string | null>){
            state.returnDate = action.payload
        },
        setTravellers(state, action: PayloadAction<Travellers>){
            state.travellers = action.payload
        },
        setRoute(state, action: PayloadAction<{ from: Airport | null; to: Airport | null }>){
            state.from = action.payload.from
            state.to= action.payload.to
        },
    },
})

export const { setFrom, setTo, swapCities,
    setDeparture, setReturnDate, setTravellers,
    setRoute
} = searchSlice.actions
export default searchSlice.reducer
