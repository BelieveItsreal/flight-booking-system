import {useAppDispatch} from "../../store/hooks.ts";
import {useState} from "react";
import {findAirport} from "../../data/airports.ts";
import type {SearchQuery} from "../../utils/searchParams.ts";
import {formatShortDate} from "../../utils/date.ts";
import {loadSearch} from "../../store/searchSlice.ts";
import {ArrowRight, MapPin, Search} from "lucide-react";
import {formatTravellers} from "../../utils/travellers.ts";
import SearchCard from "../SearchCard/SearchCard.tsx";

interface SearchSummaryBarProps{
    search: SearchQuery
}

const Dot = () => <span aria-hidden="true" className="text-muted">·</span>
function SearchSummaryBar({ search }: SearchSummaryBarProps) {
    const dispatch = useAppDispatch()
    const [isEditing, setIsEditing] = useState(false)
    const fromCity = findAirport(search.from)?.city
    const toCity = findAirport(search.to)?.city
    const dates = search.returnDate
        ? `${formatShortDate(search.departure)} – ${formatShortDate(search.returnDate)}`
        : formatShortDate(search.departure)
    const toggleEditing = () => {
        if (!isEditing) dispatch(loadSearch(search))
        setIsEditing(!isEditing)
    }
    return(
        <div className="space-y-4">
            <div className="flex flex-col gap-3 rounded-xl border border-primary/10 bg-primary/5 px-4 py-3
                 sm:flex-row sm:items-center sm:justify-between"
            >
                <p className="flex flex-wrap items-center gap-x-2 gap-y-1 text-[15px] text-ink">
                    <MapPin aria-hidden="true" className="size-5 shrink-0 text-muted" />
                    <span>
                        <strong className="font-semibold">{search.from}</strong>
                        {fromCity && ` (${fromCity})`}
                    </span>
                    <ArrowRight aria-hidden="true" className="size-4 shrink-0" />
                    <span className="sr-only">to</span>
                    <span>
                        <strong className="font-semibold">{search.to}</strong>
                        {toCity && ` (${toCity})`}
                    </span>
                    <Dot />
                    <span>{dates}</span>
                    <Dot />
                    <span>{formatTravellers(search)}</span>
                </p>
                <button
                    type="button"
                    onClick={toggleEditing}
                    aria-expanded={isEditing}
                    aria-controls="modify-search"
                    className="flex shrink-0 items-center justify-center gap-2 rounded-lg bg-primary px-3.5 py-1.5
                    text-[15px] font-semibold text-white transition hover:bg-primary-light active:scale-[0.98]"
                >
                    <Search aria-hidden="true" className="size-4" />
                    Modify Search
                </button>
            </div>
            {isEditing && (
                <div id="modify-search" className="flex justify-center">
                    <SearchCard onSearch={() => setIsEditing(false)} />
                </div>
            )}
        </div>
    )
}

export default SearchSummaryBar
