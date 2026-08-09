/**
 * @param {...(any|Object|Array<any|Object|Array>)} args
 * @return {string}
 */
export default function classNames(...args) {
	console.log("args", args);
	let str = "";
	for (const v of args) {
		if (!v) continue;
		console.log(v);
		const type = typeof v;
		if (type === "object") {
			if (Array.isArray(v)) {
				str += ` ${classNames(...v)}`;
			} else {
				for (const k in v) {
					if (v[k]) {
						str += ` ${k}`;
					}
				}
			}
		}
		if (type === "string" || type === "number") {
			str += ` ${v}`.trimEnd();
		}
	}

	return str.trim();
}
