import { Suspense } from 'react';
import { ErrorBoundary, type FallbackProps } from 'react-error-boundary';
import { LoadingOrError } from '@/components/LoadingOrError';
import { Button } from './components/ui/button';
import DeviceManagement from './pages/DeviceManagement';
import RealTimeDashboard from './pages/RealTimeDashboard';
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';

function renderError({ error }: FallbackProps) {
	return <LoadingOrError error={error} />;
}

export function App() {
	return (
		<ErrorBoundary fallbackRender={renderError}>
			<Suspense fallback={<LoadingOrError />}>
				<Router>
					<Routes>
						<Route path="/" element={<DeviceManagement />} />
						<Route path="/dashboard" element={<RealTimeDashboard />} />
					</Routes>
				</Router>
			</Suspense>
			<Button>Click me</Button>
		</ErrorBoundary>
	);
}
