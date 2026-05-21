import { BrowserRouter as Router, Routes, Route, Navigate } from "react-router";
import SignIn from "./pages/AuthPages/SignIn";
import NotFound from "./pages/OtherPage/NotFound";
import AppLayout from "./layout/AppLayout";
import { ScrollToTop } from "./components/common/ScrollToTop";
import Home from "./pages/Dashboard/Home";
import { AuthProvider } from "./context/AuthContext";
import RequireAuth from "./components/auth/RequireAuth";
import UserList from "./pages/Users/List";
import UserCreate from "./pages/Users/Create";
import CityList from "./pages/Cities/List";
import CityForm from "./pages/Cities/Form";
import CategoryList from "./pages/Categories/List";
import TagList from "./pages/Tags/List";
import MerchantList from "./pages/Merchants/List";
import MerchantForm from "./pages/Merchants/Form";
import LogList from "./pages/Logs/List";

export default function App() {
  return (
    <AuthProvider>
      <Router>
        <ScrollToTop />
        <Routes>
          <Route path="/signin" element={<SignIn />} />

          <Route
            element={
              <RequireAuth>
                <AppLayout />
              </RequireAuth>
            }
          >
            <Route index path="/" element={<Home />} />
            <Route path="/users" element={<UserList />} />
            <Route path="/users/create" element={<UserCreate />} />
            <Route path="/cities" element={<CityList />} />
            <Route path="/cities/create" element={<CityForm />} />
            <Route path="/cities/:id/edit" element={<CityForm />} />
            <Route path="/categories" element={<CategoryList />} />
            <Route path="/tags" element={<TagList />} />
            <Route path="/merchants" element={<MerchantList />} />
            <Route path="/merchants/create" element={<MerchantForm />} />
            <Route path="/merchants/:id/edit" element={<MerchantForm />} />
            <Route path="/logs" element={<LogList />} />
          </Route>

          <Route path="/signup" element={<Navigate to="/signin" replace />} />
          <Route path="*" element={<NotFound />} />
        </Routes>
      </Router>
    </AuthProvider>
  );
}
