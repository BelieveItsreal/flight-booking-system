import type {Flight} from "../data/flights.ts";
import {Link, useSearchParams} from "react-router";
import {useEffect, useMemo, useState} from "react";
import {parseSearchParams} from "../utils/searchParams.ts";
import {searchFlights} from "../api/flightApi.ts";
import {findAirport} from "../data/airports.ts";
import SearchSummaryBar from "../components/results/SearchSummaryBar.tsx";
import FlightCard from "../components/results/FlightCard.tsx";
import type { CabinClass } from "../store/searchSlice.ts";
import { sortFlights, type SortKey } from "../utils/sortFlights.ts";
import SortTabs from "../components/results/SortTabs.tsx";


interface SearchResult{
    key: string,
    flights: Flight[]
}
function SearchResults(){
    const [searchParams] = useSearchParams()
    const searchKey = searchParams.toString()
    const search = useMemo(() => parseSearchParams(searchParams), [searchParams])
    const  [result, setResult] = useState<SearchResult | null>(null)
    const [sortKey, setSortKey] = useState<SortKey>('cheapest')
    useEffect(() => {
        if (!search) return
        let ignore = false
        searchFlights(search).then((flights) => {
            if (!ignore) setResult({key: searchKey, flights})
        })
        return () => {
            ignore = true
        }
    }, [search, searchKey])

    const isLoading = search !== null && result?.key !== searchKey
    const flights = isLoading ? [] : (result?.flights ?? [])
    if (!search){
        return(
            <div className="min-h-[calc(100vh-4rem)] bg-page py-16">
                <div className="wrapper text-center">
                    <h1 className="text-xl font-semibold text-ink">Your search result is incomplete</h1>
                    <p className="mt-2 text-muted">Please choose where you're flying from and to.</p>
                    <Link to="/" className="mt-4 inline-block font-medium text-accent hover:underline">
                        ← Back to search
                    </Link>
                </div>
            </div>
        )
    }

    const fromCity = findAirport(search.from)?.city ?? search.from
    const toCity = findAirport(search.to)?.city ?? search.to
    const sortedFlights = sortFlights(flights, sortKey, search.cabinClass)
    const handleBook = (flight: Flight, cabin: CabinClass) => {
        console.log('Book', flight.flightNumber, cabin, flight.fares[cabin].price)
    }
    return (
        <div className="min-h-[calc(100vh-4rem)] bg-page py-6">
            <div className="wrapper space-y-5">
                <h1 className="sr-only">
                    Flights from {fromCity} to {toCity}
                </h1>
                <SearchSummaryBar search={search}/>
                <div className="grid grid-cols-1 items-start gap-6 lg:grid-cols-[290px_1fr]">
                    <aside aria-label="Filters" className="rounded-xl border border-line bg-surface p-4 text-sm text-muted">
                        Filters sidebar
                    </aside>
                    <section aria-label="Flight results" aria-busy={isLoading}>
                        {/* Steps 4–6: SortTabs + FlightCards, step 9: Pagination */}
                        {isLoading ? (
                            <p className="text-muted">Searching flights…</p>
                        ) : flights.length === 0 ? (
                            // Shown inside the layout, so the summary bar and Modify Search stay available
                            <p className="rounded-xl bg-surface p-6 text-center text-muted shadow-card">
                                No flights found for this route. Try a different date or city.
                            </p>
                        ) : (
                            <>
                                <SortTabs flights={flights} cabin={search.cabinClass} value={sortKey} onChange={setSortKey} />
                                <ul className="mt-4 space-y-4">
                                    {sortedFlights.slice(0, 5).map((flight) => (
                                        <li key={flight.id}>
                                            <FlightCard flight={flight} defaultCabin={search.cabinClass} onBook={handleBook} />
                                        </li>
                                    ))}
                                </ul>
                            </>
                        )}
                    </section>
                </div>
            </div>
        </div>
    )
}

export default SearchResults