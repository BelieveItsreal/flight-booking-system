import {useAppDispatch} from "../../store/hooks.ts";
import {setRoute} from "../../store/searchSlice.ts";
import {findAirport} from "../../data/airports.ts";
import {POPULAR_ROUTES, type PopularRoute} from "../../data/popularRoutes.ts";
import RouteCard from "./RouteCard.tsx";

function PopularRoutes() {
    const dispatch = useAppDispatch()
    const handleBook = (route: PopularRoute) => {
        dispatch(setRoute({from: findAirport(route.from), to: findAirport(route.to)}))
        window.scrollTo({top: 0, behavior: 'smooth'})
    }
    return (
        <section aria-label="popular routes" className="relative z-10 -mt-10 pb-12">
            <div className="wrapper">
                {/* Mobile: swipeable row. scroll-px-4 makes cards snap to the padded edge, not the screen edge;
                    pt-2/pb-6 give the shadow room, because a scroll container clips everything outside it */}
                <div className="scrollbar-none -mx-4 flex snap-x snap-mandatory scroll-px-4 gap-4 overflow-x-auto px-4 pt-2 pb-6
                    sm:mx-auto sm:grid sm:max-w-220 sm:grid-cols-2 sm:gap-6 sm:overflow-visible sm:px-0 sm:pt-0 sm:pb-0 lg:grid-cols-3">
                    {POPULAR_ROUTES.map((route) => (
                        <RouteCard key={route.id} route={route} onBook={handleBook} />
                    ))}
                </div>
            </div>
        </section>
    )
}

export default PopularRoutes
