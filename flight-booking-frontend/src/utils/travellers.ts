import type {Travellers} from "../store/searchSlice.ts";

export function formatTravellers({ adults, children, infants, cabinClass }: Travellers): string{
    const total = adults + children + infants
    return `${total} Traveller${total > 1 ? 's' : ''}, ${cabinClass}`
}