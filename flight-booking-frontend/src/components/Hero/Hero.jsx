import {heroWingSunset} from "../../assets/index.js";
import SearchCard from "../SearchCard/SearchCard.jsx";

function Hero() {
    return(
        <section className="relative min-h-82.5 pt-12 pb-24">
           <img
               src={heroWingSunset}
               alt=""
               fetchPriority="high"
               className="absolute inset-0 size-full object-cover"
           />
            <div className="absolute inset-0 bg-linear-to-b from-primary/40 via-primary/10 to-transparent"/>
            <div className="wrapper relative flex justify-center">
                <SearchCard />
            </div>
        </section>
    )
}
export default Hero