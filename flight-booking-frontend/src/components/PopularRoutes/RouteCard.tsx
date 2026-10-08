import { ArrowRight, Plane } from 'lucide-react'
import { findAirport } from '../../data/airports.ts'
import type { PopularRoute } from '../../data/popularRoutes.ts'
import { formatINR } from '../../utils/currency.ts'

interface RouteCardProps {
    route: PopularRoute
    onBook: (route: PopularRoute) => void
}

function RouteCard({route, onBook}: RouteCardProps){
    const from = findAirport(route.from)
    const to = findAirport(route.to)
    return(
        <article className="group rounded-xl bg-surface p-2.5 shadow-card transition duration-300
        hover:-translate-y-1 hover:shadow-float">
            <div className="overflow-hidden rounded-lg">
                <img
                    src={route.image}
                    alt={route.imageAlt}
                    loading="lazy"
                    className="aspect-2/1 w-full object-cover transition-transform duration-500 group-hover:scale-105"
                />
            </div>
            <div className="px-2 pt-3 pb-1.5">
                <h3 className="flex items-center gap-1.5 text-lg font-semibold text-ink">
                    {from?.city}
                    <ArrowRight className="size-4" />
                    {to?.city}
                </h3>
                <p className="mt-0.5 flex items-center gap-1.5 text-xs text-muted">
                    <Plane className="size-3.5 text-ink" />
                    {route.tripType}
                </p>
                <div className="mt-3 flex items-center justify-between">
                    <p className="text-2xl font-bold text-accent">{formatINR(route.price)}</p>
                    <button
                        type="button"
                        onClick={() => onBook(route)}
                        className="rounded-md border border-line px-3 py-1.5 text-sm font-medium text-ink
                        transition duration-150 hover:border-primary hover:bg-primary hover:text-white active:scale-95"
                    >
                    Book Now
                    </button>
                </div>
            </div>
        </article>
    )
}
export default RouteCard
