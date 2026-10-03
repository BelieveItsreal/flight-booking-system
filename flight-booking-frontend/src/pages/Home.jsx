import Hero from "../components/Hero/Hero.jsx";
import PopularRoutes from "../components/PopularRoutes/PopularRoutes.jsx";
import HowItWorks from "../components/HowItWorks/HowItWorks.jsx";

function Home(){
    return(
        <>
            <Hero />
            <PopularRoutes />
            <HowItWorks />
        </>
    )
}
export default Home