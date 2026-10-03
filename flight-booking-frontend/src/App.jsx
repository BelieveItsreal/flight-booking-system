import { Route, Routes } from 'react-router'
import Layout from './components/Layout/Layout.jsx'
import Home from './pages/Home.jsx'
import MyBookings from './pages/MyBookings.jsx'
import Offers from './pages/Offers.jsx'
import Support from './pages/Support.jsx'
import NotFound from './pages/NotFound.jsx'
import SearchResults from "./pages/SearchResults.jsx";

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
