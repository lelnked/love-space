import { BrowserRouter as Router, Routes, Route, Navigate } from "react-router";
import SignIn from "./pages/AuthPages/SignIn";
import NotFound from "./pages/OtherPage/NotFound";
import AppLayout from "./layout/AppLayout";
import { ScrollToTop } from "./components/common/ScrollToTop";
import { AuthProvider } from "./context/AuthContext";
import { ToastProvider } from "./context/ToastContext";
import { ConfirmProvider } from "./context/ConfirmContext";
import RequireAuth from "./components/auth/RequireAuth";
import ManagerList from "./pages/Managers/List";
import CityList from "./pages/Cities/List";
import CityForm from "./pages/Cities/Form";
import BannerList from "./pages/Banners/BannerList";
import BannerForm from "./pages/Banners/BannerForm";
import CategoryList from "./pages/Categories/List";
import TagList from "./pages/Tags/List";
import MerchantList from "./pages/Merchants/List";
import MerchantForm from "./pages/Merchants/Form";
import MerchantDetailPage from "./pages/Merchants/Detail";
import RecommendListList from "./pages/RecommendLists/List";
import RecommendListForm from "./pages/RecommendLists/Form";
import RecommendListMerchants from "./pages/RecommendLists/Merchants";
import AmbassadorList from "./pages/Ambassadors/List";
import RouteList from "./pages/Routes/List";
import RouteForm from "./pages/Routes/Form";
import ActivityList from "./pages/Activities/List";
import ActivityForm from "./pages/Activities/Form";
import ArticleCategoryList from "./pages/ArticleCategories/List";
import ArticleList from "./pages/Articles/List";
import ArticleForm from "./pages/Articles/Form";
import FeaturedItemList from "./pages/FeaturedItems/List";
import FeaturedCycleItemList from "./pages/FeaturedCycleItems/List";
import LogList from "./pages/Logs/List";

export default function App() {
  return (
    <AuthProvider>
      <ToastProvider>
      <ConfirmProvider>
      <Router basename="/love-space">
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
            <Route index path="/" element={<Navigate to="/cities" replace />} />
            <Route path="/managers" element={<ManagerList />} />
            <Route path="/cities" element={<CityList />} />
            <Route path="/cities/create" element={<CityForm />} />
            <Route path="/cities/:id/edit" element={<CityForm />} />
            <Route path="/banners" element={<BannerList />} />
            <Route path="/banners/new" element={<BannerForm />} />
            <Route path="/banners/:id/edit" element={<BannerForm />} />
            <Route path="/categories" element={<CategoryList />} />
            <Route path="/tags" element={<TagList />} />
            <Route path="/merchants" element={<MerchantList />} />
            <Route path="/merchants/create" element={<MerchantForm />} />
            <Route path="/merchants/:id/edit" element={<MerchantForm />} />
            <Route path="/merchants/:id" element={<MerchantDetailPage />} />
            <Route path="/recommend-lists" element={<RecommendListList />} />
            <Route path="/recommend-lists/create" element={<RecommendListForm />} />
            <Route path="/recommend-lists/:id/edit" element={<RecommendListForm />} />
            <Route path="/recommend-lists/:id/merchants" element={<RecommendListMerchants />} />
            <Route path="/ambassadors" element={<AmbassadorList />} />
            <Route path="/routes" element={<RouteList />} />
            <Route path="/routes/create" element={<RouteForm />} />
            <Route path="/routes/:id/edit" element={<RouteForm />} />
            <Route path="/activities" element={<ActivityList />} />
            <Route path="/activities/create" element={<ActivityForm />} />
            <Route path="/activities/:id/edit" element={<ActivityForm />} />
            <Route path="/article-categories" element={<ArticleCategoryList />} />
            <Route path="/articles" element={<ArticleList />} />
            <Route path="/articles/create" element={<ArticleForm />} />
            <Route path="/articles/:id/edit" element={<ArticleForm />} />
            <Route path="/featured-items" element={<FeaturedItemList />} />
            <Route path="/featured-cycle-items" element={<FeaturedCycleItemList />} />
            <Route path="/logs" element={<LogList />} />
          </Route>

          <Route path="/signup" element={<Navigate to="/signin" replace />} />
          <Route path="*" element={<NotFound />} />
        </Routes>
      </Router>
      </ConfirmProvider>
      </ToastProvider>
    </AuthProvider>
  );
}
