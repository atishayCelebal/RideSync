import {lazy, Suspense} from 'react'
import {ErrorBoundary, type FallbackProps} from 'react-error-boundary'
import {Route, Routes} from 'react-router'
import {LoadingOrError} from '@/components/LoadingOrError'
import {Gallery} from '@/pages/Gallery'
import { Button } from './components/ui/button'

const Details = lazy(async () =>
	import('@/pages/Details').then(m => ({default: m.Details}))
)

function renderError({error}: FallbackProps) {
	return <LoadingOrError error={error} />
}

export function App() {
	return (
		<ErrorBoundary fallbackRender={renderError}>
			<Suspense fallback={<LoadingOrError />}>
				<Routes>
					<Route element={<Gallery />} index={true} />
					<Route element={<Details />} path=':fruitName' />
				</Routes>
			</Suspense>
			 <Button>Click me</Button>
		</ErrorBoundary>
	)
}
