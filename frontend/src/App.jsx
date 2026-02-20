import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import Navbar from './components/Navbar';
import Footer from './components/Footer';
import Landing from './pages/Landing';
import Home from './pages/Home';
import MealList from './pages/MealList';
import MealDetail from './pages/MealDetail';
import AddMeal from './pages/AddMeal';

function PlaceholderPage({ title }) {
  return (
    <div className="flex justify-center items-center h-64 bg-white rounded-3xl border border-gray-100 shadow-sm mt-8">
      <h1 className="text-3xl font-bold text-gray-900">{title} (Coming Soon)</h1>
    </div>
  );
}

export default function App() {
  return (
    <Router>
      <div className="min-h-screen flex flex-col font-sans bg-gray-50 text-gray-900">
        <Navbar />
        <main className="flex-1 w-full max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
          <Routes>
            <Route path="/" element={<Landing />} />
            <Route path="/home" element={<Home />} />
            <Route path="/meals" element={<MealList />} />
            <Route path="/meals/:id" element={<MealDetail />} />
            <Route path="/add-meal" element={<AddMeal />} />
            <Route path="/ai-query" element={<PlaceholderPage title="AI Query Feature" />} />
            <Route path="/profile" element={<PlaceholderPage title="User Profile" />} />
          </Routes>
        </main>
        <Footer />
      </div>
    </Router>
  );
}
