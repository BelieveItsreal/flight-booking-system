import { Route, Routes } from 'react-router'
import Layout from './components/Layout/Layout.tsx'
import Home from './pages/Home.tsx'
import MyBookings from './pages/MyBookings.tsx'
import Offers from './pages/Offers.tsx'
import Support from './pages/Support.tsx'
import NotFound from './pages/NotFound.tsx'
import SearchResults from "./pages/SearchResults.tsx";

function App() {
    return (
        <Routes>
            <Route element={<Layout />}>
                <Route path="/" element={<Home />} />
                <Route path="/my-bookings" element={<MyBookings />} />
                <Route path="/offers" element={<Offers />} />
                <Route path="/support" element={<Support />} />
                <Route path="/search" element={<SearchResults />} />
                <Route path="*" element={<NotFound />} />
            </Route>
        </Routes>
    )
}

export default App
