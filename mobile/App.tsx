import { ScreenContent } from 'components/ScreenContent';
import { StatusBar } from 'expo-status-bar';

import './global.css';

export default function App() {
  return (
    <>
      <ScreenContent title="Homeeee" path="App.tsx"></ScreenContent>
      <StatusBar style="auto" />
    </>
  );
}
