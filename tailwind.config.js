/** @type {import('tailwindcss').Config} */
export default {
	content: ["./src/main/resources/**/*.{html,js}"],
	theme: {
		extend: {
			fontFamily: {
				greatVibes: ['Great Vibes', 'cursive'],
				montserrat: ['Montserrat', 'sans-serif'],
			},
			colors: {
				orange: {
					DEFAULT: '#E47A2E',  // Extending the default 'orange' color
					500: '#E47A2E',      // Optionally define specific shades
				},
			},
		},
	},
	plugins: [],
	darkMode: "selector",
}