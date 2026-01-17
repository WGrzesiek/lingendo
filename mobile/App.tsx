import { ScreenContent } from 'components/ScreenContent';
import { StatusBar } from 'expo-status-bar';

import './global.css';
import { Button } from './components/nativewindui/Button';
import { Text } from './components/nativewindui/Text';

export default function App() {
  return (
    <>
      <ScreenContent title="Homeeee" path="App.tsx"></ScreenContent>
      <StatusBar style="auto" />

    </>
  );
}
