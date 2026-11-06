import { defineConfig } from '@playwright/test';
export default defineConfig({ testDir: './tests', workers: 1, reporter: [['list'], ['html', { open: 'never' }]], use: { baseURL: 'http://127.0.0.1:8080', trace: 'retain-on-failure' } });
