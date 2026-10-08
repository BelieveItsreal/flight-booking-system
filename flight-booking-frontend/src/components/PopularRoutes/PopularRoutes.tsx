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
                <div className="mx-auto grid max-w-220 grid-cols-3 gap-6">
                    {POPULAR_ROUTES.map((route) => (
                        <RouteCard key={route.id} route={route} onBook={handleBook} />
                    ))}
                </div>
            </div>
        </section>
    )
}

export default PopularRoutes
