import type {CabinClass} from "../store/searchSlice.ts";
import type {FlightServiceParams} from "../api/flightApi.ts";
import {isValid, parseISO} from "date-fns";
import {toISODate} from "./date.ts";

export type SearchQuery =FlightServiceParams & { returnDate: string | null }
function isCabinClass(value: string | null): value is CabinClass{
    return value === 'Economy' || value === 'Business'
}

function toCount(value: string | null, fallback: number, min: number){
    const count = Number(value)
    return value != null && Number.isInteger(count) && count >= min ? count : fallback
}

const  isValidDate = (iso: string | null): iso is string => iso != null && isValid(parseISO(iso))
export function parseSearchParams(params: URLSearchParams): SearchQuery | null {
    const from = params.get('from')?.toUpperCase()
    const to = params.get('to')?.toUpperCase()
    if (!from || !to) return null
    const depart = params.get('depart')
    const departure = isValidDate(depart) ? depart : toISODate(new Date())
    const returnParam = params.get('return')
    const cabinClass = params.get('class')
    return {
        from,
        to,
        departure,
        returnDate: isValidDate(returnParam) && returnParam >= departure ? returnParam : null,
        adults: toCount(params.get('adults'), 1, 1),
        children: toCount(params.get('children'), 0, 0),
        infants: toCount(params.get('infants'), 0, 0),
        cabinClass: isCabinClass(cabinClass)? cabinClass: 'Economy',
    }
}