import { createRouter, createWebHistory } from "vue-router";

import HomeView from "../views/HomeView.vue";
import OperadorasView from "../views/OperadorasView.vue";
import OperadoraDetalheView from "../views/OperadoraDetalheView.vue";
import EstatisticasView from "../views/EstatisticasView.vue";
import DespesasUFView from "../views/DespesasUFView.vue";

const routes = [
  {
    path: "/",
    redirect: "/home",
  },
  {
    path: "/home",
    component: HomeView,
  },
  {
    path: "/operadoras",
    component: OperadorasView,
  },
  {
    path: "/operadoras/:cnpj",
    component: OperadoraDetalheView,
    props: true,
  },
  {
    path: "/estatisticas",
    component: EstatisticasView,
  },
  {
    path: "/despesas-uf",
    component: DespesasUFView,
  },
];

export default createRouter({
  history: createWebHistory(),
  routes,
});
