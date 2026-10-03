import {findAirport} from "../data/airports.js";
import {toISODate} from "../utils/date.js";
import {createSlice} from "@reduxjs/toolkit";

const initialState = {
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
        setFrom(state, action){
            state.from = action.payload
        },
        setTo(state, action){
            state.to = action.payload
        },
        swapCities(state){
            const oldFrom = state.from
            state.from = state.to
            state.to = oldFrom
        },
        setDeparture(state, action){
            state.departure = action.payload
            if (state.returnDate && state.returnDate < action.payload){
                state.returnDate = null
            }
        },
        setReturnDate(state, action){
            state.returnDate = action.payload
        },
        setTravellers(state, action){
            state.travellers = action.payload
        },
        setRoute(state, action){
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