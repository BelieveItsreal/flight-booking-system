import Hero from "../components/Hero/Hero.tsx";
import PopularRoutes from "../components/PopularRoutes/PopularRoutes.tsx";
import HowItWorks from "../components/HowItWorks/HowItWorks.tsx";

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
