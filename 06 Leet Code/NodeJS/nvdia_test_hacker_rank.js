weater = [{
    city : "Adelaide",
    weather : "15 degree",
    status: {
        Wind: "114 mph",
        Humidity: "91%",
    }
}, {
    city : "Adelaide",
    weather : "15 degree",
    status: {
        Wind: "114 mph",
        Humidity: "91%",
    }
}];

const rs = weater.map(w => {
    const keys = Object.keys(w);
    const s = keys.map(k => {
        let s1 = "";
        if (k === "city") {
            s1 += w[k];
        }
        if (k === "weather") {
            s1 += w[k].split(" ")[0];
        }
        if (k === "status") {
            s1 += Object.keys(w[k]).map(k1 => {
                let s2 = "";
                if (k1 === "Wind") {
                    s2 += w[k][k1].split(" ")[0];
                }
                if (k1 === "Humidity") {
                    s2 += w[k][k1].split("%")[0];
                }
                return s2;
            })
        }
        return s1;
    });

    return s.join(",");
});

console.log(rs)
