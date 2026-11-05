import { Link } from 'react-router'
function NotFound(){
    return(
        <div className="wrapper py-8">
            <h1>404 – Page not found</h1>
            <Link to="/" className="text-accent">Go back home</Link>
        </div>
    )
}
export default NotFound
