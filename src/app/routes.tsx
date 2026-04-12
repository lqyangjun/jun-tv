import { createBrowserRouter } from 'react-router';
import Home from './pages/Home';
import Search from './pages/Search';
import History from './pages/History';
import Detail from './pages/Detail';
import Category from './pages/Category';
import Profile from './pages/Profile';
import Settings from './pages/Settings';
import ClearCache from './pages/ClearCache';

export const router = createBrowserRouter([
  {
    path: '/',
    Component: Home,
  },
  {
    path: '/search',
    Component: Search,
  },
  {
    path: '/history',
    Component: History,
  },
  {
    path: '/detail/:id',
    Component: Detail,
  },
  {
    path: '/category/:id',
    Component: Category,
  },
  {
    path: '/profile',
    Component: Profile,
  },
  {
    path: '/settings',
    Component: Settings,
  },
  {
    path: '/clear-cache',
    Component: ClearCache,
  },
]);