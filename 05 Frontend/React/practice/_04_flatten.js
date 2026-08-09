/**
 * @param {Array<*|Array>} value
 * @return {Array}
 */
export default function flatten(value) {
	let acc = [];

	for (let i = 0; i < value.length; i++) {
		if (typeof value[i] === "object" && Array.isArray(value[i])) {
			acc = acc.concat(flatten(value[i]));
		} else {
			acc.push(value[i]);
		}
	}

	return acc;
}
