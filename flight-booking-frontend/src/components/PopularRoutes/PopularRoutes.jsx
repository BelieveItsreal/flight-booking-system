import {useDispatch} from "react-redux";
import {setRoute} from "../../store/searchSlice.js";
import {findAirport} from "../../data/airports.js";
import {POPULAR_ROUTES} from "../../data/popularRoutes.js";
import RouteCard from "./RouteCard.jsx";

function PopularRoutes() {
    const dispatch = useDispatch()
    const handleBook = (route) => {
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
